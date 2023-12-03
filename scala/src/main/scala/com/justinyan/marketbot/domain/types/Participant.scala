package com.justinyan.marketbot.domain.types

import com.justinyan.marketbot.domain.types.Participant.ParticipantId

case class Participant(id: ParticipantId)

object Participant {
  type ParticipantId = String
}
