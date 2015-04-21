package com.ankamagames.baseImpl.common.clientAndServer.game.HMIAction;

import com.ankamagames.framework.external.*;
import com.ankamagames.baseImpl.common.clientAndServer.game.HMIAction.actions.*;

public enum HMIActionType implements ExportableEnum
{
    SOUND((byte)1, "Son", HMISoundAction.class), 
    PARTICLE_SYSTEM((byte)2, "Particules", HMIParticleSystemAction.class), 
    LIGHT_SOURCE((byte)3, "Lumiere", HMILightSourceAction.class), 
    APPEARANCE_CHANGE((byte)4, "Changement d'Apparance", HMIChangeAppearanceAction.class), 
    CAMERA_SHAKE((byte)5, "Tremblement de la camera", HMICameraShakeAction.class), 
    SKIN_PART_OTHER_CHANGE((byte)6, "Changement d'une(des) partie(s) venant d'un autre skin", HMIChangeSkinPartOtherAction.class), 
    SKIN_COLOR_CHANGE((byte)8, "Changement de couleur de parties", HMIChangeSkinColorAction.class), 
    ANIM_SUFFIX_CHANGE((byte)9, "Changement du suffixe des anim", HMIChangeAnimSuffixAction.class), 
    ANIM_STATIC_CHANGE((byte)10, "Changement de l'anim statique", HMIChangeAnimStaticAction.class), 
    SKIN_PART_VISIBILITY((byte)11, "Affihe/masque une(des) partie(s)", HMIVisibilitySkinPartAction.class), 
    SET_UI_PROPERTY((byte)12, "Passe des valeurs aux interfaces", HMISetUiProperty.class), 
    LINK_MOBILE((byte)13, "Attache un mobile au point d'accroche LienClip", HMILinkMobileAction.class), 
    ANIM_CHANGE((byte)14, "Applique une anim", HMIChangeAnimAction.class), 
    INCREMENT_ALTITUDE((byte)15, "Incr\u00e9mente l'altitude", HMIIncrementAltitudeAction.class), 
    CHANGE_MOVEMENT_STYLE((byte)16, "Change le style de mouvement", HMIChangeMovementStyle.class), 
    SET_MONSTER_SKIN((byte)17, "Applique l'apparence d'un monstre", HMISetMonsterSkinAction.class), 
    HIDE_ALL_EQUIPMENTS((byte)18, "Masque tous les \u00e9quipements du joueur", HMIHideAllEquipmentsAction.class), 
    COSTUME((byte)19, "Costumes - Insignes", HMICostumeAction.class);
    
    private final byte m_id;
    private final String m_label;
    private final Class<? extends HMIAction> m_class;
    
    public static HMIActionType getFromId(final byte id) {
        for (final HMIActionType type : values()) {
            if (id == type.m_id) {
                return type;
            }
        }
        throw new IllegalArgumentException("HMIActionType inconnu : '" + id + "'");
    }
    
    private HMIActionType(final byte id, final String typeLabel, final Class<? extends HMIAction> c) {
        this.m_id = id;
        this.m_label = typeLabel;
        this.m_class = c;
    }
    
    public short getId() {
        return this.m_id;
    }
    
    public String getLabel() {
        return this.m_label;
    }
    
    public Class<? extends HMIAction> getRepresentationClass() {
        return this.m_class;
    }
    
    @Override
    public String getEnumId() {
        return Short.toString(this.getId());
    }
    
    @Override
    public String getEnumLabel() {
        return this.getLabel();
    }
    
    @Override
    public String getEnumComment() {
        return this.getLabel();
    }
}
