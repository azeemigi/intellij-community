/*
 * Copyright 2004-2005 Alexey Efimov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.intellij.images.fileTypes.impl;

import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.fileTypes.*;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import gnu.trove.THashSet;
import org.intellij.images.ImagesBundle;
import org.intellij.images.fileTypes.ImageFileTypeManager;
import org.intellij.images.vfs.IfsUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.util.Set;

/**
 * Image file type manager.
 *
 * @author <a href="mailto:aefimov.box@gmail.com">Alexey Efimov</a>
 */
final class ImageFileTypeManagerImpl extends ImageFileTypeManager implements ApplicationComponent {
  @NonNls private static final String NAME = "ImagesFileTypeManager";

  @NonNls private static final String IMAGE_FILE_TYPE_NAME = "Images";
  private static final String IMAGE_FILE_TYPE_DESCRIPTION = ImagesBundle.message("images.filetype.description");
  private static final UserFileType imageFileType;

  static {
    imageFileType = new ImageFileType();
    imageFileType.setIcon(IconLoader.getIcon("/org/intellij/images/icons/ImagesFileType.png"));
    imageFileType.setName(IMAGE_FILE_TYPE_NAME);
    imageFileType.setDescription(IMAGE_FILE_TYPE_DESCRIPTION);
  }

  public ImageFileTypeManagerImpl() {
  }

  public boolean isImage(VirtualFile file) {
    FileTypeManager fileTypeManager = FileTypeManager.getInstance();
    FileType fileTypeByFile = file.getFileType();
    return fileTypeByFile instanceof ImageFileType;
  }

  public FileType getImageFileType() {
    return imageFileType;
  }

  @NotNull
  public String getComponentName() {
    return NAME;
  }

  public void initComponent() {
  }

  public void disposeComponent() {
  }

  public static final class ImageFileType extends UserBinaryFileType {
  }

  public void createFileTypes(final @NotNull FileTypeConsumer consumer) {
    final Set<String> processed = new THashSet<String>();

    final String[] readerFormatNames = ImageIO.getReaderFormatNames();
    for (String format : readerFormatNames) {
      final String ext = format.toLowerCase();
      processed.add(ext);
    }

    processed.add(IfsUtil.ICO_FORMAT.toLowerCase());

    consumer.consume(imageFileType, StringUtil.join(processed, FileTypeConsumer.EXTENSION_DELIMITER));
  }
}
