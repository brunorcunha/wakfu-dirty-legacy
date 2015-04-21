package com.ankamagames.wakfu.common.game.effect.runningEffect;

import java.util.*;

import com.ankamagames.baseImpl.common.clientAndServer.game.effect.*;
import com.ankamagames.baseImpl.common.clientAndServer.utils.*;
import com.ankamagames.baseImpl.common.clientAndServer.game.effect.runningEffect.*;
import com.ankamagames.wakfu.common.game.fighter.*;
import com.ankamagames.framework.kernel.core.common.*;

import org.apache.commons.pool.*;

import com.ankamagames.framework.external.*;
import com.ankamagames.wakfu.common.game.effect.*;

public class HPGainForCasterBasedOnTarget extends WakfuRunningEffect
{
    private static final BitSet EMPTY_BIT_SET;
    private static final ObjectPool m_staticPool;
    private static final ParameterListSet PARAMETERS_LIST_SET;
    private int m_percent;
    private boolean m_fixedValue;
    private Elements m_element;
    
    @Override
    public ParameterListSet getParametersListSet() {
        return HPGainForCasterBasedOnTarget.PARAMETERS_LIST_SET;
    }
    
    protected HPGainForCasterBasedOnTarget() {
        super();
        this.m_fixedValue = false;
    }
    
    public HPGainForCasterBasedOnTarget(final Elements element) {
        super();
        this.m_fixedValue = false;
        this.m_element = element;
        this.setTriggersToExecute();
    }
    
    @Override
    public Elements getElement() {
        return this.m_element;
    }
    
    public static HPGainForCasterBasedOnTarget checkOut(final EffectContext<WakfuEffect> context, final Elements element) {
        HPGainForCasterBasedOnTarget re;
        try {
            re = (HPGainForCasterBasedOnTarget)HPGainForCasterBasedOnTarget.m_staticPool.borrowObject();
            re.m_pool = HPGainForCasterBasedOnTarget.m_staticPool;
        }
        catch (Exception e) {
            re = new HPGainForCasterBasedOnTarget();
            re.m_pool = null;
            re.m_isStatic = false;
            RunningEffect.m_logger.error("Erreur lors d'un checkOut sur un HPGainForCasterBasedOnTarget : " + e.getMessage());
        }
        re.m_element = element;
        re.m_id = RunningEffectConstants.HP_GAIN.getId();
        re.m_status = RunningEffectConstants.HP_GAIN.getObject().getRunningEffectStatus();
        re.m_maxExecutionCount = -1;
        re.m_context = context;
        return re;
    }
    
    @Override
    public HPGainForCasterBasedOnTarget newInstance() {
        HPGainForCasterBasedOnTarget re;
        try {
            re = (HPGainForCasterBasedOnTarget)HPGainForCasterBasedOnTarget.m_staticPool.borrowObject();
            re.m_pool = HPGainForCasterBasedOnTarget.m_staticPool;
        }
        catch (Exception e) {
            re = new HPGainForCasterBasedOnTarget();
            re.m_pool = null;
            re.m_isStatic = false;
            RunningEffect.m_logger.error("Erreur lors d'un checkOut sur un HPGainForCasterBasedOnTarget : " + e.getMessage());
        }
        re.m_element = this.m_element;
        return re;
    }
    
    @Override
    public void release() {
        super.release();
    }
    
    @Override
    public void setTriggersToExecute() {
        super.setTriggersToExecute();
        this.m_triggers.set(1);
    }
    
    @Override
    public void update(final int whatToUpdate, final float howMuchToUpate, final boolean set) {
        super.update(whatToUpdate, howMuchToUpate, set);
        switch (whatToUpdate) {
            case 0: {
                if (!set) {
                    this.m_value += (int)(this.m_value * howMuchToUpate / 100.0f);
                    break;
                }
                break;
            }
            case 1: {
                if (set) {
                    this.m_value = ValueRounder.randomRound(howMuchToUpate);
                    break;
                }
                this.m_value += (int)howMuchToUpate;
                break;
            }
        }
    }
    
    @Override
    protected void executeOverride(final RunningEffect linkedRE, final boolean trigger) {
        this.setNotified(true);
        final WakfuRunningEffect hpgain = HPGain.checkOut(this.m_context, this.getElement());
        hpgain.forceValue(this.m_value);
        (hpgain).setGenericEffect(this.m_genericEffect);
        hpgain.setCaster(this.m_target);
        hpgain.setTarget(this.m_caster);
        hpgain.askForExecution();
    }
    
    @Override
    public void effectiveComputeValue(final RunningEffect triggerRE) {
        this.extractParams();
        if (this.m_target == null || !this.m_target.hasCharacteristic(FighterCharacteristicType.HP) || this.m_caster == null || !this.m_caster.hasCharacteristic(FighterCharacteristicType.HP)) {
            this.m_value = 0;
        }
        else {
            final int targetLifePoints = this.m_target.getCharacteristic(FighterCharacteristicType.HP).max();
            this.m_value = targetLifePoints * this.m_percent / 100;
        }
        if (this.m_fixedValue) {
            return;
        }
        final int percentModificator = this.getValuePercentModificator();
        this.m_value += this.m_value * percentModificator / 100;
        this.m_value = Math.max(0, ValueRounder.randomRound(this.m_value));
    }
    
    public void extractParams() {
        final short level = this.getContainerLevel();
        if (this.m_genericEffect == null) {
            return;
        }
        final int paramsCount = this.m_genericEffect.getParamsCount();
        if (paramsCount >= 1) {
            this.m_percent = this.m_genericEffect.getParam(0, level, RoundingMethod.LIKE_PREVIOUS_LEVEL);
        }
        if (paramsCount >= 2) {
            this.m_fixedValue = (this.m_genericEffect.getParam(1, level, RoundingMethod.LIKE_PREVIOUS_LEVEL) == 1);
        }
        else {
            this.m_fixedValue = false;
        }
    }
    
    private int getValuePercentModificator() {
        if (this.m_caster == null) {
            return 0;
        }
        int percentModificator = 0;
        if (this.m_caster.hasCharacteristic(this.m_element.getMasteryCharacteristic())) {
            percentModificator += this.m_caster.getCharacteristicValue(this.m_element.getMasteryCharacteristic());
        }
        if (this.m_caster.hasCharacteristic(FighterCharacteristicType.HEAL_IN_PERCENT)) {
            percentModificator += this.m_caster.getCharacteristicValue(FighterCharacteristicType.HEAL_IN_PERCENT);
        }
        if (this.m_caster.hasCharacteristic(FighterCharacteristicType.RES_HEAL_PERCENT)) {
            percentModificator -= this.m_caster.getCharacteristicValue(FighterCharacteristicType.RES_HEAL_PERCENT);
        }
        return percentModificator;
    }
    
    @Override
    public boolean useCaster() {
        return true;
    }
    
    @Override
    public boolean useTarget() {
        return true;
    }
    
    @Override
    public boolean useTargetCell() {
        return false;
    }
    
    @Override
    public void onCheckIn() {
        this.m_fixedValue = false;
        super.onCheckIn();
    }
    
    @Override
    public BitSet getTriggersToExecute() {
        if (this.m_caster != null && this.m_caster.isActiveProperty(FightPropertyType.UNDEAD)) {
            return HPGainForCasterBasedOnTarget.EMPTY_BIT_SET;
        }
        return super.getTriggersToExecute();
    }
    
    static {
        EMPTY_BIT_SET = new BitSet();
        m_staticPool = new MonitoredPool(new ObjectFactory<HPGainForCasterBasedOnTarget>() {
            @Override
            public HPGainForCasterBasedOnTarget makeObject() {
                return new HPGainForCasterBasedOnTarget();
            }
        });
        PARAMETERS_LIST_SET = new WakfuRunningEffectParameterListSet(new ParameterList[] { new WakfuRunningEffectParameterList("% PdV de la cibles gagn\u00e9s", new WakfuRunningEffectParameter[] { new WakfuRunningEffectParameter("valeur", WakfuRunningEffectParameterType.VALUE) }), new WakfuRunningEffectParameterList("% PdV de la cibles gagn\u00e9s", new WakfuRunningEffectParameter[] { new WakfuRunningEffectParameter("valeur", WakfuRunningEffectParameterType.VALUE), new WakfuRunningEffectParameter("fixe (1 = oui, 0 = non (defaut))", WakfuRunningEffectParameterType.CONFIG) }) });
    }
}
