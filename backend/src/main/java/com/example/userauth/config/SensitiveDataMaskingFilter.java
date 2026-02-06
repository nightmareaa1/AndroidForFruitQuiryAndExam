package com.example.userauth.config;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Logback filter for masking sensitive information in log messages.
 * Masks passwords, tokens, credit card numbers, and other PII data.
 *
 * @author UserAuth Team
 * @version 1.0
 */
public class SensitiveDataMaskingFilter extends Filter<ILoggingEvent> {

  private static final List<Pattern> SENSITIVE_PATTERNS = Arrays.asList(
      // Password patterns
      Pattern.compile("(password|passwd|pwd)\\s*[:=]\\s*\\S+", Pattern.CASE_INSENSITIVE),
      // JWT/Bearer token patterns
      Pattern.compile("(bearer\\s+)\\S+", Pattern.CASE_INSENSITIVE),
      Pattern.compile("(token|jwt)\\s*[:=]\\s*\\S+", Pattern.CASE_INSENSITIVE),
      // Credit card patterns (15-19 digits)
      Pattern.compile("\\b\\d{4}[-\\s]?\\d{4}[-\\s]?\\d{4}[-\\s]?\\d{4,7}\\b"),
      // Email patterns
      Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"),
      // Phone number patterns
      Pattern.compile("\\b\\d{3}[-.]?\\d{3}[-.]?\\d{4}\\b"),
      // API Key patterns
      Pattern.compile("(api[_-]?key|apikey)\\s*[:=]\\s*\\S+", Pattern.CASE_INSENSITIVE),
      // Secret patterns
      Pattern.compile("(secret|private[_-]?key)\\s*[:=]\\s*\\S+", Pattern.CASE_INSENSITIVE)
  );

  private static final String MASK = "***MASKED***";

  @Override
  public FilterReply decide(ILoggingEvent event) {
    // Always allow the event, but the masking happens in the encoder
    return FilterReply.NEUTRAL;
  }

  /**
   * Masks sensitive data in the given message.
   *
   * @param message the original log message
   * @return the message with sensitive data masked
   */
  public static String maskSensitiveData(String message) {
    if (message == null || message.isEmpty()) {
      return message;
    }

    String maskedMessage = message;
    for (Pattern pattern : SENSITIVE_PATTERNS) {
      Matcher matcher = pattern.matcher(maskedMessage);
      maskedMessage = matcher.replaceAll(MASK);
    }

    return maskedMessage;
  }
}
