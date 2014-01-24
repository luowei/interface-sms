package net.oilchem.common.utils;

import static net.oilchem.common.utils.SecretUtils.decryptMode;
import static net.oilchem.common.utils.SecretUtils.encryptMode;

/**
 * Created with IntelliJ IDEA.
 * User: luowei
 * Date: 14-1-24
 * Time: 上午9:48
 * To change this template use File | Settings | File Templates.
 */
public class TokenUtil {

    /**
     * <p>
     * 根据sessionId和UserId生成令牌
     * </p>
     * @param sessionId
     * @param userId
     * @return
     */
    public static String createToken(String sessionId, Long userId) {
        if (sessionId != null && userId != null) {
            String originalToken = sessionId + "," + System.currentTimeMillis() + "," + userId;
            return new String(encryptMode(originalToken.getBytes()));
        }
        return null;
    }

    /**
     * <p>
     * 根据用户令牌获取标准用户ID
     * </p>
     * @param token
     * @return
     */
    public static Long getUserIdFromToken(String token) {
        if (token != null) {
            String originalToken = new String(decryptMode(token.getBytes()));
            int beginIndex = originalToken.lastIndexOf(",");
            String userId = originalToken.substring(beginIndex + 1);
            return new Long(userId);
        }
        return null;
    }

    /**
     * <p>
     * 根据用户令牌获取SessionId
     * </p>
     * @param token
     * @return
     */
    public static Long getSessionIdFromToken(String token) {
        if (token != null) {
            String originalToken = new String(decryptMode(token.getBytes()));
            int index = originalToken.indexOf(",");
            String sessionId = originalToken.substring(0, index);
            return new Long(sessionId);
        }
        return null;
    }
}
