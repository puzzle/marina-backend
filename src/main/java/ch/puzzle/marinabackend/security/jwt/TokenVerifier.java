package ch.puzzle.marinabackend.security.jwt;

import ch.puzzle.marinabackend.KeycloakAuthenticationExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.time.Instant;
import java.util.Map;

@Component
public class TokenVerifier {

    private static final Logger LOG = LoggerFactory.getLogger(TokenVerifier.class);

    @Value("${security.oauth2.client.public-key-modulus}")
    private String n;

    @Value("${security.oauth2.client.public-key-exponent}")
    private String e;

    private final KeycloakAuthenticationExtractor keycloakExtractor;

    public TokenVerifier(KeycloakAuthenticationExtractor extractor) {
        this.keycloakExtractor = extractor;
    }

    public Authentication getAuthentication(String authToken) {
        Jwt jwt = JwtHelper.decodeAndVerify(authToken, getVerifier());
        Map<String, Object> claims = parseClaims(jwt.getClaims());
        OAuth2Authentication auth = extractAuthentication(claims);
        auth.setDetails(authToken);
        return auth;
    }

    public boolean validateToken(String authToken) {
        try {
            Jwt jwt = JwtHelper.decodeAndVerify(authToken, getVerifier());
            Map<String, Object> claims = parseClaims(jwt.getClaims());
            Instant expiration = keycloakExtractor.extractExpiration(claims);
            return expiration.isAfter(Instant.now());
        } catch (Exception e) {
            LOG.info("Could not verify token.");
            LOG.trace("Could not verify token: {}", e);
        }
        return false;
    }

    private OAuth2Authentication extractAuthentication(Map<String, ?> map) {
        Authentication user = keycloakExtractor.extractAuthentication(map);
        OAuth2Request request = new OAuth2Request(null, null, user.getAuthorities(), true, null, null, null, null, null);
        return new OAuth2Authentication(request, user);
    }

    private RsaVerifier getVerifier() {
        return new RsaVerifier(exponentFormatToRSA(n, e));
    }

    private Map<String, Object> parseClaims(String claims) {
        JsonParser parser = JsonParserFactory.getJsonParser();
        return parser.parseMap(claims);
    }

    private RSAPublicKey exponentFormatToRSA(String n, String e) {
        BigInteger modulus = new BigInteger(1, Base64Utils.decodeFromUrlSafeString(n));
        BigInteger publicExponent = new BigInteger(1, Base64Utils.decodeFromUrlSafeString(e));
        try {
            return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(modulus, publicExponent));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
