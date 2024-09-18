package org.vane.hub

import com.intellij.ide.actions.RevealFileAction
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.FileTypeRegistry
import com.intellij.openapi.updateSettings.impl.pluginsAdvertisement.installAndEnable
import com.intellij.openapi.vfs.VirtualFile
import java.io.File

object VaneFileUtil {
    fun isFileOfType(file: VirtualFile, type: FileType): Boolean = FileTypeRegistry.getInstance().isFileOfType(file, type)

    fun open(file: File) {
        if (file.isFile)
            RevealFileAction.openFile(file)
        else if (file.isDirectory)
            RevealFileAction.openDirectory(file)

    }
}