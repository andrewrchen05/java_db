import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class timestamp {
    public static void main(String[] args) {
        LocalDateTime date = LocalDateTime.now();
        System.out.println(date);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        System.out.println(dtf.format(date));

    }
}

    