package com.yapp.itemfinder.domain.item

import javax.persistence.Embeddable

@Embeddable
class ItemPin(
    var positionX: Float,
    var positionY: Float
)
