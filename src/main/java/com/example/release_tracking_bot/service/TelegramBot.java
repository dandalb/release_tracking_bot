package com.example.release_tracking_bot.service;

import com.example.release_tracking_bot.config.BotConfig;
import com.example.release_tracking_bot.model.Artist;
import com.example.release_tracking_bot.model.User;
import com.example.release_tracking_bot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.swing.table.TableRowSorter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Log4j2
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    private final ReleaseTrackerService releaseTrackerService;
    private final UserRepository userRepository;
    private final ArtistService artistService;
    private final MessageCreateService messageCreateService;
    private final UserService userService;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String userName = update.getMessage().getChat().getFirstName();

            if (messageText.startsWith("/add")) {
                String message = messageText.substring("/add".length()).trim();
                addArtists(message, chatId);
                return;
            }//switch, check limit, update message, create server

            if (messageText.startsWith("/id")) {
                String message = messageText.substring("/id".length()).trim();
                addArtistById(message, chatId);
                return;
            }

            if (messageText.startsWith("/delete")) {
                String message = messageText.substring("/delete".length()).trim();
                deleteArtist(chatId, message);
                return;
            }

            switch (messageText) {
                case "/start":
                    userService.saveUser(userName, String.valueOf(chatId));
                    sendMessage(chatId, messageCreateService.startCommandReceived(userName));
                    break;
                case "/all":
                    sendLongMessage(
                            chatId,
                            messageCreateService.allArtistMessage(userRepository.findByChatId(String.valueOf(chatId)).getArtists()
                                    .stream()
                                    .sorted(Comparator.comparing(Artist::getName))
                                    .collect(Collectors.toList())));
                    break;
                default:
                    sendMessage(chatId, "Command: '" + messageText + "' not supported");
            }
        }
    }

    private void sendLongMessage(long chatId, List<String> messages) {

        if (messages.size() > 25) {
            List<String> subMessages = messages.subList(0, 25);
            sendMessage(chatId, messageCreateService.parseUpdateMessages(subMessages));
            messages.removeAll(subMessages);
            sendLongMessage(chatId, messages);
        } else {
            sendMessage(chatId, messageCreateService.parseUpdateMessages(messages));
        }
    }

    private void sendMessage(long chatId, String messageToSend) {

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(messageToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Can't execute message: " + message, e);
        }
    }

    @Scheduled(fixedDelay = 1000 * 60 * 2)
    private void checkReleaseUpdates() {
        log.info("Start checkReleaseUpdates");

        Map<String, List<Artist>> updatedReleases = releaseTrackerService.checkLastUpdates();
        for (Map.Entry<String, List<Artist>> userArtistsMap : updatedReleases.entrySet()) {
            sendLongMessage(Long.parseLong(userArtistsMap.getKey()),
                    messageCreateService.releaseUpdatedMessage(userArtistsMap.getValue()));
        }
        log.info("Artists with updated releases: " + updatedReleases.values());
    }

    private void addArtistById(String artistMessage, long userChatId) {
        User user = userRepository.findByChatId(String.valueOf(userChatId));
        List<Artist> userArtists = user.getArtists();
        List<Artist> requestedArtists = new ArrayList<>();
        try {
            for (String artistId : artistMessage.split(",")) {
                String artist = artistId.trim();

                if (!artist.isEmpty()) {
                    Artist requestedArtist = artistService.requestArtistById(artistId);
                    Artist existArtist = artistService.findByArtistId(requestedArtist.getId());

                    if (existArtist != null) {
                        if (!userArtists.contains(existArtist)) {
                            userArtists.add(existArtist);
                        }
                    } else
                        requestedArtists.add(requestedArtist);
                }
            }
        } catch (RuntimeException e) {
            sendMessage(userChatId, e.getMessage());
            throw new RuntimeException("Error occurred while adding new artist", e);
        }

        userArtists.addAll(artistService.saveAllArtists(requestedArtists));
        user.setArtists(userArtists);
        userRepository.save(user);
        sendMessage(userChatId, "Artists: " + requestedArtists.stream().map(Artist::getName).toList() + " was added");
    }

    private void addArtists(String artistMessage, long userChatId) {
        try {
            User user = userRepository.findByChatId(String.valueOf(userChatId));
            List<Artist> userArtists = user.getArtists();
            List<Artist> requestedArtists = new ArrayList<>();

            for (String artistString : artistMessage.split(",")) {
                String artist = artistString.trim();

                if (!artist.isEmpty()) {
                    Artist requestedArtist = artistService.requestArtistByName(artistString);
                    Artist existArtist = artistService.findByArtistId(requestedArtist.getId());

                    if (existArtist != null) {
                        if (!userArtists.contains(existArtist)) {
                            userArtists.add(existArtist);
                        }
                    } else
                        requestedArtists.add(requestedArtist);
                }
            }

            userArtists.addAll(artistService.saveAllArtists(requestedArtists));
            user.setArtists(userArtists);
            userRepository.save(user);
            sendMessage(userChatId, "Artists: " + artistMessage + " was added");
        } catch (Exception e) {
            sendMessage(userChatId,
                    "Can't add artists by name: [" + artistMessage +
                            "]\n\n" +
                            "Valid example: /add Drake, Evgeniy Bounty");

            throw new RuntimeException("Error occurred while adding new artist", e);
        }
    }

    private void deleteArtist(long chatId, String artistName) {
        User user = userRepository.findByChatId(String.valueOf(chatId));
        List<Artist> userArtists = user.getArtists();
        int artistsCount = userArtists.size();

        for (Artist artist : userArtists) {
            if (artist.getName().equals(artistName.trim())) {
                userArtists.remove(artist);
                break;
            }
        }

        if (artistsCount == userArtists.size()) {
            sendMessage(chatId, "Artist by name: '" + artistName + "' not found" +
                    "\nUse command '/all' for check your artists");
            return;
        }
        user.setArtists(userArtists);
        userRepository.save(user);
        sendMessage(chatId, "Artist '" + artistName + "' was deleted successful");
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }
}
