package org.vane.hub

import com.intellij.ide.plugins.IdeaPluginDependency
import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.extensions.PluginId
import com.intellij.util.Icons

object VanePluginUtil {
    private const val VANE_HUB: String = "org.vane.hub"
    private val VANE_HUB_ID: PluginId = PluginId.getId(VANE_HUB)

    fun getVanePlugins(): Collection<IdeaPluginDescriptor> {
        val pluginIds: MutableList<IdeaPluginDescriptor> = mutableListOf()
        for (plugin: IdeaPluginDescriptor in PluginManagerCore.getPlugins()) {
            if (plugin.dependencies.haveVaneHub())
                pluginIds.add(plugin)
        }
        return pluginIds
    }

    fun isNotEmptyVanePlugin(): Boolean {
        for (plugin: IdeaPluginDescriptor in PluginManagerCore.getPlugins()) {
            if (plugin.dependencies.haveVaneHub())
                return true
        }
        return false
    }

    private fun List<IdeaPluginDependency>.haveVaneHub(): Boolean {
        if (isEmpty()) return false
        for (dependency: IdeaPluginDependency in this) {
            if (dependency.pluginId == VANE_HUB_ID)
                return true
        }
        return false
    }
}