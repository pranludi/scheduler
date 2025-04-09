package io.pranludi.scheduler.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "log")
public class LogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long no;
    private String contents;
    private String createAt;
    private String updateAt;

    public LogEntity() {
    }

    public LogEntity(String contents, String createdAt, String updateAt) {
        this.contents = contents;
        this.createAt = createdAt;
        this.updateAt = updateAt;
    }

    public long getNo() {
        return no;
    }

    public String getContents() {
        return contents;
    }

    public String getCreateAt() {
        return createAt;
    }

    public String getUpdateAt() {
        return updateAt;
    }

}