package com.yapp.itemfinder.domain.entity.item

import javax.persistence.Embeddable

@Embeddable
class ItemPin(
    var positionX: Float,
    var positionY: Float
)
