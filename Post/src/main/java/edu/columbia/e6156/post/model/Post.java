package edu.columbia.e6156.post.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;


@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Post {
    @Id
    @Type(type="org.hibernate.type.UUIDCharType")
    private UUID postId;

    @Type(type="org.hibernate.type.UUIDCharType")
    private UUID userId;

    @Type(type="org.hibernate.type.UUIDCharType")
    private UUID imageId;

    private String subject;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;
}
