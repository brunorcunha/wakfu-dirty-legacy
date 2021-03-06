package com.ankamagames.wakfu.client.core.contentInitializer;

import org.apache.log4j.*;
import com.ankamagames.wakfu.client.binaryStorage.*;
import com.ankamagames.wakfu.common.datas.Breed.*;
import com.ankamagames.framework.fileFormat.io.binaryStorage2.*;
import com.ankamagames.wakfu.client.core.*;

public final class MonsterFamilyPestLoader implements ContentInitializer
{
    private static final Logger m_logger;
    
    @Override
    public void init() throws Exception {
        BinaryDocumentManager.getInstance().foreach(new MonsterTypePestBinaryData(), new LoadProcedure<MonsterTypePestBinaryData>() {
            @Override
            public void load(final MonsterTypePestBinaryData bs) {
                final int familyId = bs.getFamilyId();
                final int pestMonsterId = bs.getPestMonsterId();
                final MonsterFamily monsterFamily = MonsterFamilyManager.getInstance().getMonsterFamily(familyId);
                if (monsterFamily == null) {
                    MonsterFamilyPestLoader.m_logger.error("On ajoute un nuisible \u00e0 une famille de monstre inconnue : familyId=" + familyId);
                    return;
                }
                monsterFamily.addPestMonster(pestMonsterId);
            }
        });
    }
    
    @Override
    public String getName() {
        return WakfuTranslator.getInstance().getString("contentLoader.monsterFamilyPest");
    }
    
    static {
        m_logger = Logger.getLogger(MonsterFamilyPestLoader.class);
    }
}
