package app.DTOs;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyLogDTO {

    private Integer id;
    private LocalDate createdAt;
}
