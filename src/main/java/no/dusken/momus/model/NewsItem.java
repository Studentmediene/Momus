package no.dusken.momus.model;

import lombok.*;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {}, callSuper = true)
@ToString(of = {"title", "date", "author"}, callSuper = true)
@Builder(toBuilder = true)
public class NewsItem extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private ZonedDateTime date;

    @ManyToOne(fetch = FetchType.EAGER)
    private Person author;

    private String content;

}
