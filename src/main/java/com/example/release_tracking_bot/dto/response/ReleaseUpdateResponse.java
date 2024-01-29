package com.example.release_tracking_bot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReleaseUpdateResponse {
    private String name;
    private String link;

    @Override
    public String toString() {
        return name + "\n" + "Link: " + link;
    }
}
