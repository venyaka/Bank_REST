package com.example.bankcards.service.impl;

import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.example.bankcards.config.RestTemplateConfig;
import com.example.bankcards.constant.IpAddressesConstant;
import com.example.bankcards.dto.response.IpStackResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.UserSession;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.exception.errors.NotFoundError;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.repository.UserSessionRepository;
import com.example.bankcards.service.SessionService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final RestTemplate restTemplate; // Используем один бин RestTemplate

    @Value("${ipstack.access.key}")
    private String accessKey;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public UserSession saveNewSession(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(NotFoundError.USER_NOT_FOUND));

        // Завершаем старые сессии перед созданием новой
        endOldSessions(user);

        HttpServletRequest request = getRequest();
        String uAgent = request.getHeader("User-Agent");
        UserAgent userAgent = UserAgent.parseUserAgentString(uAgent);
        OperatingSystem os = userAgent.getOperatingSystem();
        String ip = getClientIp(request);
        String cityName = getCityFromIp(ip);

        UserSession userSession = new UserSession();
        userSession.setUser(user);
        userSession.setIpAddress(ip);
        userSession.setCity(cityName != null ? cityName : "Unknown");
        userSession.setUserAgent(uAgent);
        userSession.setOsName(os.getName());
        userSession.setDeviceType(os.getDeviceType().getName());
        userSession.setStartTime(LocalDateTime.now());

        return userSessionRepository.save(userSession);
    }

    /**
     * Завершает все активные сессии для указанного пользователя.
     *
     * @param user Пользователь, чьи сессии нужно завершить.
     */
    @Transactional
    public void endOldSessions(User user) {
        List<UserSession> activeSessions = userSessionRepository.findByUserAndEndTimeIsNull(user);
        if (!activeSessions.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            for (UserSession session : activeSessions) {
                session.setEndTime(now);
            }
            userSessionRepository.saveAll(activeSessions);
        }
    }

    /**
     * Получает город по IP-адресу с помощью сервиса ipstack.
     *
     * @param ip IP-адрес.
     * @return Название города или null в случае ошибки.
     */
    private String getCityFromIp(String ip) {
        // Не делаем запрос для локальных/тестовых IP
        if (ip == null || ip.equals("127.0.0.1") || ip.equals("0:0:0:0:0:0:0:1")) {
            return "Local";
        }
        String url = IpAddressesConstant.API_IPSTACK_URL + ip + "?access_key=" + accessKey;
        try {
            IpStackResponse response = restTemplate.getForObject(url, IpStackResponse.class);
            if (response != null && response.getCity() != null) {
                return response.getCity();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Получает текущий HttpServletRequest.
     *
     * @return HttpServletRequest.
     * @throws IllegalStateException если запрос недоступен.
     */
    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new IllegalStateException("Could not find current HttpServletRequest");
        }
        return attributes.getRequest();
    }

    /**
     * Получает IP-адрес клиента, учитывая прокси.
     *
     * @param request HttpServletRequest.
     * @return IP-адрес клиента.
     */
    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty() || "unknown".equalsIgnoreCase(xfHeader)) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
