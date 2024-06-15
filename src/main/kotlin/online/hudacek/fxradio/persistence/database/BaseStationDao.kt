/*
 *     FXRadio - Internet radio directory
 *     Copyright (C) 2020  hudacek.online
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package online.hudacek.fxradio.persistence.database

import io.reactivex.rxjava3.core.Flowable
import online.hudacek.fxradio.apiclient.radiobrowser.model.Station
import online.hudacek.fxradio.persistence.database.entity.StationEntity

/**
 * Common operations on database of stations with different tables (e.g. History, Favourites ...)
 */
abstract class BaseStationDao : Dao<Station, StationEntity> {

    override fun selectAll(): Flowable<StationEntity> = selectAllQuery().autoMap(StationEntity::class.java)

    override fun removeAll(): Flowable<Int> = removeAllQuery().counts()

    override fun remove(element: Station): Flowable<Int> =
        database.update("DELETE FROM $tableName WHERE stationuuid = ?")
            .parameter(element.uuid)
            .counts()
}
