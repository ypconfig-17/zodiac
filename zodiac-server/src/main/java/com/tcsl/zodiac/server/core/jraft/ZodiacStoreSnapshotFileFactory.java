package com.tcsl.zodiac.server.core.jraft;

import com.tcsl.zodiac.server.core.store.JraftStroe;
import com.tcsl.zodiac.server.core.store.MemoryDBStroe;

/**
 * @program: libra
 * @description:
 * @author: Yang Pan
 * @create: 2021-08-07:15:46
 */
public class ZodiacStoreSnapshotFileFactory {



   public static ZodiacStoreSnapshotFile getLibraStoreSnapshotFile(JraftStroe jraftStroe){
       ZodiacStoreSnapshotFile storeSnapshotFile = null;
       if (jraftStroe instanceof MemoryDBStroe){
           storeSnapshotFile = new MemoryZodiacStoreSnapshotFile((MemoryDBStroe) jraftStroe);
       }else {

       }

       return storeSnapshotFile;
   }
}

