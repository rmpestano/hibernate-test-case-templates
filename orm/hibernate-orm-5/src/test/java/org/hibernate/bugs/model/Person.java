package org.hibernate.bugs.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by pestano on 13/08/16.
 */
@Entity(name = "Person")
public class Person {

    @Id
    private Long id;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Phone> phones = new ArrayList<>();

    public Person() {
    }

    public Person(Long id, Collection<Phone> phones) {
        this.id = id;
        this.phones = new ArrayList<>(phones);
    }

    public void addPhone(Phone phone) {
        phones.add( phone );
        phone.setPerson(this);
    }

    public List<Phone> getPhones() {
        return phones;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
