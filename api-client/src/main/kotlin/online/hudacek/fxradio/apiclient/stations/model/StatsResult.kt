/*
 *  Copyright 2020 FXRadio by hudacek.online
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package online.hudacek.fxradio.apiclient.stations.model

data class StatsResult(val supported_version: String,
                       val software_version: String,
                       val status: String,
                       val stations: String,
                       val stations_broken: String,
                       val tags: String,
                       val clicks_last_hour: Int,
                       val clicks_last_day: Int,
                       val languages: Int,
                       val countries: Int
)