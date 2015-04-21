package com.ankamagames.wakfu.client.core.game.breed;

import org.apache.log4j.*;

public class BreedSkinAndHairManager
{
    private static final Logger m_logger;
    private static final BreedSkinAndHairManager m_instance;
    private static final byte HAIR_MASK = 1;
    private static final byte SKIN_MASK = 2;
    private static final byte PUPIL_MASK = 4;
    
    public static BreedSkinAndHairManager getInstance() {
        return BreedSkinAndHairManager.m_instance;
    }
    
    public boolean getHairAvailability(final int breedId, final byte sex) {
        return getColorAvailability((byte)1, breedId, sex);
    }
    
    public boolean getSkinAvailability(final int breedId, final byte sex) {
        return getColorAvailability((byte)2, breedId, sex);
    }
    
    public boolean getPupilAvailability(final int breedId, final byte sex) {
        return getColorAvailability((byte)4, breedId, sex);
    }
    
    private static boolean getColorAvailability(final byte mask, final int breedId, final byte sex) {
    	return true;
    }
    
    static {
        m_logger = Logger.getLogger(BreedSkinAndHairManager.class);
        m_instance = new BreedSkinAndHairManager();
    }
}
