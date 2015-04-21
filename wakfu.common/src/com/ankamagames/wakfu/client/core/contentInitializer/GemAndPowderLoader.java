package com.ankamagames.wakfu.client.core.contentInitializer;

import org.apache.log4j.*;
import com.ankamagames.wakfu.client.binaryStorage.*;
import com.ankamagames.wakfu.common.game.item.*;
import com.ankamagames.wakfu.common.game.item.gems.*;
import com.ankamagames.wakfu.common.game.item.referenceItem.*;
import com.ankamagames.framework.fileFormat.io.binaryStorage2.*;

public class GemAndPowderLoader implements ContentInitializer
{
    private static final Logger m_logger;
    
    @Override
    public void init() throws Exception {
        this.execute();
    }
    
    public void execute() throws Exception {
        BinaryDocumentManager.getInstance().foreach(new GemAndPowderBinaryData(), new LoadProcedure<GemAndPowderBinaryData>() {
            @Override
            public void load(final GemAndPowderBinaryData data) {
                final AbstractReferenceItem gem = ReferenceItemManager.getInstance().getReferenceItem(data.getId());
                if (gem == null) {
                    GemAndPowderLoader.m_logger.warn("Impossible de trouver la gemme/poudre d'id " + data.getId());
                    return;
                }
                try {
                    BasicGemsDefinitionManager.INSTANCE.registerGem(gem.getGemElementType(), gem.getRarity(), gem.getLevel(), gem.getId());
                }
                catch (IllegalArgumentException e) {
                    GemAndPowderLoader.m_logger.error("Gemme d'id " + gem.getId() + " - " + e.getMessage(), e);
                }
            }
        });
    }
    
    @Override
    public String getName() {
        return "";
    }
    
    static {
        m_logger = Logger.getLogger(GemAndPowderLoader.class);
    }
}
