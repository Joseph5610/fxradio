package online.hudacek.fxradio.persistence.database.entity

import org.davidmoten.rxjava3.jdbc.annotations.Column

interface StationEntity {

    @Column("ID")
    fun id(): Int

    @Column
    fun name(): String

    @Column("stationuuid")
    fun uuid(): String

    @Column
    fun urlResolved(): String

    @Column
    fun homepage(): String

    @Column
    fun favicon(): String?

    @Column
    fun tags(): String?

    @Column
    fun country(): String?

    @Column("countrycode")
    fun countryCode(): String?

    @Column
    fun state(): String?

    @Column
    fun language(): String?

    @Column
    fun codec(): String?

    @Column
    fun bitrate(): Int

    @Column
    fun hasExtendedInfo(): Int
}