package no.dusken.momus.model;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"article", "requester", "status"})
@Builder(toBuilder = true)
public class IllustrationRequest {
    @Id
    private Long id;

    @JoinColumn(name = "id")
    @OneToOne(fetch = FetchType.LAZY)
    private Article article;

    @ManyToOne
    private Person requester;

    private String description;

    private String illustratorComment;

    private int numberOfIllustrations;

    private int numberOfPages;

    private boolean isExternal;

    private String contactEmail;

    private LocalDate dueDate;

    private LocalDateTime created;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        PENDING, ACCEPTED, DENIED, COMPLETED
    }
}
