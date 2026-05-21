package com.project.inno_online_store.jpa.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;


@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
@NoArgsConstructor()
@AllArgsConstructor
public class User extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    private String name;

    private String surname;

    @Column(name = "birth_date", updatable = false)
    private LocalDate birthDate;

    @Column(updatable = false)
    private String email;

    @Column(name = "active")
    private Boolean isActive;

    @OneToMany(mappedBy = "user", orphanRemoval = true,cascade = CascadeType.ALL)
    @ToString.Exclude
    private Set<PaymentCard> paymentCards;

    @Override
    public final boolean equals(Object o) {
        if(this == o) return true;
        if(o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy
                ?((HibernateProxy) o).getHibernateLazyInitializer()
                 .getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy
                ?((HibernateProxy) this).getHibernateLazyInitializer()
                 .getPersistentClass()
                : this.getClass();
        if(thisEffectiveClass != oEffectiveClass) return false;
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer()
                  .getPersistentClass()
                  .hashCode()
                : getClass().hashCode();
    }
}
