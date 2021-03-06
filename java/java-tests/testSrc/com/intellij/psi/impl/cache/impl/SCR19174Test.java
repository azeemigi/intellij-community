package com.intellij.psi.impl.cache.impl;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.PsiTestCase;

import java.io.File;
import java.io.IOException;

/**
 * @author max
 */
public class SCR19174Test extends PsiTestCase {
  private VirtualFile myDir;
  private VirtualFile myVFile;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    final File root = FileUtil.createTempFile(getName(), "");
    root.delete();
    root.mkdir();
    myFilesToDelete.add(root);

    ApplicationManager.getApplication().runWriteAction(new Runnable() {
      @Override
      public void run() {
        try {
          VirtualFile rootVFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(root.getAbsolutePath().replace(File.separatorChar, '/'));

          myDir = rootVFile.createChildDirectory(null, "contentAndLibrary");

          /*
          myVFile = myDir.createChildData(null, "A.java");
          Writer writer1 = myVFile.getWriter(null);
          writer1.write("package p; public class A{ public void foo(); }");
          writer1.close();
          */

          final ModifiableRootModel rootModel = ModuleRootManager.getInstance(myModule).getModifiableModel();
          final ContentEntry contentEntry1 = rootModel.addContentEntry(myDir);
          contentEntry1.addSourceFolder(myDir, false);
          rootModel.commit();
        }
        catch (IOException e) {
          LOG.error(e);
        }
      }
    });
  }

  private void changeRoots() {
    ApplicationManager.getApplication().runWriteAction(new Runnable() {
      public void run() {
        final ModifiableRootModel rootModel = ModuleRootManager.getInstance(myModule).getModifiableModel();

        final Library jarLibrary = rootModel.getModuleLibraryTable().createLibrary();
        final Library.ModifiableModel libraryModel = jarLibrary.getModifiableModel();
        libraryModel.addRoot(myDir, OrderRootType.CLASSES);
        libraryModel.commit();
        rootModel.commit();
      }
    });
  }

  public void testBug() throws Exception {
    touchFileSync();
    PsiFile psiFile = myPsiManager.findFile(myVFile);
    psiFile.getText();
    changeRoots();
  }

  private void touchFileSync() throws IOException {
    myVFile = myDir.createChildData(null, "A.java");
    VfsUtil.saveText(myVFile, "package p; public class A{ public void foo(); }");
  }
}
