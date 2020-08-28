package com.tpssoft.hham;

import com.tpssoft.hham.service.SearchConstraint;
import com.tpssoft.hham.service.SearchConstraints;

import javax.servlet.http.HttpServletRequest;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.function.Function;

public final class Helper {
    private Helper() {
    }

    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

    public static void addConstraintFromParam(SearchConstraints constraints,
                                              HttpServletRequest request,
                                              String paramName,
                                              Function<String, Object> valueConstructor) {
        if (request.getParameter(paramName) != null) {
            constraints.getConstraints().add(new SearchConstraint(
                    paramName,
                    valueConstructor.apply(request.getParameter(paramName)),
                    SearchConstraint.MatchMode.EQUALS
            ));
        }
    }

    public static String sha512Hash(byte[] content) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
        messageDigest.update(content);
        var sb = new StringBuilder(messageDigest.getDigestLength() * 2);
        for (byte b : messageDigest.digest()) {
            sb.append(HEX_CHARS[(b >> 4) & 0x0F]);
            sb.append(HEX_CHARS[b & 0x0F]);
        }
        return sb.toString();
    }
}
