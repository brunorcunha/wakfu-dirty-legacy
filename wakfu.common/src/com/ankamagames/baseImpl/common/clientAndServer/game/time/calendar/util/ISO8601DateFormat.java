package com.ankamagames.baseImpl.common.clientAndServer.game.time.calendar.util;

import org.apache.log4j.helpers.*;
import java.util.*;
import java.text.*;
import org.jetbrains.annotations.*;

public class ISO8601DateFormat extends AbsoluteTimeDateFormat
{
    private static long m_lastTime;
    private static final char[] m_lastTimeString;
    
    public ISO8601DateFormat() {
        super();
    }
    
    public ISO8601DateFormat(final TimeZone timeZone) {
        super(timeZone);
    }
    
    @Override
	public StringBuffer format(final Date date, final StringBuffer sbuf, final FieldPosition fieldPosition) {
        final long now = date.getTime();
        final int millis = (int)(now % 1000L);
        if (now - millis == ISO8601DateFormat.m_lastTime) {
            sbuf.append(ISO8601DateFormat.m_lastTimeString);
        }
        else {
            this.calendar.setTime(date);
            final int start = sbuf.length();
            final int year = this.calendar.get(1);
            sbuf.append(year);
            String month = null;
            switch (this.calendar.get(2)) {
                case 0: {
                    month = "-01-";
                    break;
                }
                case 1: {
                    month = "-02-";
                    break;
                }
                case 2: {
                    month = "-03-";
                    break;
                }
                case 3: {
                    month = "-04-";
                    break;
                }
                case 4: {
                    month = "-05-";
                    break;
                }
                case 5: {
                    month = "-06-";
                    break;
                }
                case 6: {
                    month = "-07-";
                    break;
                }
                case 7: {
                    month = "-08-";
                    break;
                }
                case 8: {
                    month = "-09-";
                    break;
                }
                case 9: {
                    month = "-10-";
                    break;
                }
                case 10: {
                    month = "-11-";
                    break;
                }
                case 11: {
                    month = "-12-";
                    break;
                }
                default: {
                    month = "-NA-";
                    break;
                }
            }
            sbuf.append(month);
            final int day = this.calendar.get(5);
            if (day < 10) {
                sbuf.append('0');
            }
            sbuf.append(day);
            sbuf.append(' ');
            final int hour = this.calendar.get(11);
            if (hour < 10) {
                sbuf.append('0');
            }
            sbuf.append(hour);
            sbuf.append(':');
            final int mins = this.calendar.get(12);
            if (mins < 10) {
                sbuf.append('0');
            }
            sbuf.append(mins);
            sbuf.append(':');
            final int secs = this.calendar.get(13);
            if (secs < 10) {
                sbuf.append('0');
            }
            sbuf.append(secs);
            sbuf.getChars(start, sbuf.length(), ISO8601DateFormat.m_lastTimeString, 0);
            ISO8601DateFormat.m_lastTime = now - millis;
        }
        return sbuf;
    }
    
    @Override
	@Nullable
    public Date parse(final String s, final ParsePosition pos) {
        return null;
    }
    
    static {
        m_lastTimeString = new char[20];
    }
}
