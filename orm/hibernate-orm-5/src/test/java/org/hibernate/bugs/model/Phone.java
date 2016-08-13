package org.hibernate.bugs.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Created by pestano on 13/08/16.
 */
@Entity
public class Phone {

    @Id
    @Column(name = "`number`")
    private String number;

    @ManyToOne
    private Person person;

    public Phone() {
    }

    public Phone(String number) {
        this.number = number;
    }

    public void setPerson(Person p) {
        this.person = p;
    }

    public Person getPerson() {
        return person;
    }

    public String getNumber() {
        return number;
    }
}
