package com.mangofactory.swagger.devapp.api;

import com.wordnik.swagger.annotations.ApiParam;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class UserSearch {
    private String firstName;
    private String lastName;
    @ApiParam(value = "dateOfBirth", defaultValue = "Sat, 08 Nov 2014 00:55:21 GMT")
    private Date dateOfBirth;

    @ApiParam(value = "LocalDate substituted as string", defaultValue = "2014-11-02")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate signOnDate;

    @ApiParam(value = "LocalDateTime substituted as string", defaultValue = "2014-11-02 10:22:04")
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime expireDate;

    @ApiParam(value = "Using lists", defaultValue = "a,b")
    private List<String> albums;

    @ApiParam(value = "Using sets", defaultValue = "c,d")
    private Set<String> genres;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public LocalDate getSignOnDate() {
        return signOnDate;
    }

    public void setSignOnDate(LocalDate signOnDate) {
        this.signOnDate = signOnDate;
    }

    public LocalDateTime getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDateTime expireDate) {
        this.expireDate = expireDate;
    }

    public List<String> getAlbums() {
        return albums;
    }

    public void setAlbums(List<String> albums) {
        this.albums = albums;
    }

    public Set<String> getGenres() {
        return genres;
    }

    public void setGenres(Set<String> genres) {
        this.genres = genres;
    }

    @Override
    public String toString() {
        return "ArtistSearch{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", signOnDate=" + signOnDate +
                ", expireDate=" + expireDate +
                ", albums=" + albums +
                ", genres=" + genres +
                '}';
    }
}
