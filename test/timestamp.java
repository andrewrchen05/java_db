import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class timestamp {
    public static void main(String[] args) {
        ZonedDateTime date = ZonedDateTime.now();
        System.out.println(date);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ssx");
        System.out.println(dtf.format(date));

    }
}

    