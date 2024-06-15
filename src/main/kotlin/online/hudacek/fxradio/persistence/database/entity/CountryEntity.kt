package online.hudacek.fxradio.persistence.database.entity

import org.davidmoten.rxjava3.jdbc.annotations.Column
import org.davidmoten.rxjava3.jdbc.annotations.Query

@Query("SELECT name, iso3 FROM PINNED;")
interface CountryEntity {

    @Column
    fun name(): String

    @Column
    fun iso3(): String
}