package com.ankamagames.wakfu.common.game.nation.actionRequest.impl;

import org.apache.log4j.*;
import com.ankamagames.wakfu.common.game.nation.actionRequest.*;
import com.ankamagames.wakfu.common.game.nation.*;
import java.nio.*;

public class NationProtectorBuffsClearRequest extends NationActionRequest
{
    private static final Logger m_logger;
    public static final NationActionRequestFactory FACTORY;
    private int m_protectorId;
    
    public NationProtectorBuffsClearRequest() {
        super(NationActionRequestType.PROTECTOR_BUFFS_CLEAR);
    }
    
    public void setProtectorId(final int protectorId) {
        this.m_protectorId = protectorId;
    }
    
    @Override
    public void execute() {
        final Nation nation = this.getConcernedNation();
        if (nation == null) {
            NationProtectorBuffsClearRequest.m_logger.error("Impossible d'ex\u00e9cuter l'action " + this + " : la nation " + this.m_nationId + " n'existe pas");
            return;
        }
        nation.requestClearProtectorBuffs(this.m_protectorId);
    }
    
    @Override
    public boolean authorizedFromClient(final Citizen citizen) {
        return false;
    }
    
    @Override
    public boolean serialize(final ByteBuffer buffer) {
        buffer.putInt(this.m_protectorId);
        return true;
    }
    
    @Override
    public boolean unserialize(final ByteBuffer buffer) {
        this.m_protectorId = buffer.getInt();
        return true;
    }
    
    @Override
    public int serializedSize() {
        return 4;
    }
    
    @Override
    public void clear() {
        this.m_nationId = -1;
        this.m_protectorId = -1;
    }
    
    static {
        m_logger = Logger.getLogger(NationProtectorBuffsClearRequest.class);
        FACTORY = new NationActionRequestFactory() {
            @Override
            public NationActionRequest createNew() {
                return new NationProtectorBuffsClearRequest();
            }
        };
    }
}
