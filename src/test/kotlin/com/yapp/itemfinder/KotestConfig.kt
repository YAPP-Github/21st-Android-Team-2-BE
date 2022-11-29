package com.yapp.itemfinder

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.IsolationMode
import io.kotest.extensions.spring.SpringTestExtension
import io.kotest.spring.SpringTestLifecycleMode

class KotestConfig : AbstractProjectConfig() {
    override val isolationMode = IsolationMode.InstancePerLeaf
    override fun extensions() = listOf(SpringTestExtension(SpringTestLifecycleMode.Root))
}
