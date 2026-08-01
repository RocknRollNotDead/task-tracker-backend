import { useEffect, useMemo, useRef } from "react";

/**
 * Возвращает функцию, которая откладывает вызов callback на delayMs
 * после последнего вызова (для автосохранения полей ввода без кнопки "Сохранить").
 */
export function useDebouncedCallback(callback, delayMs) {
  const callbackRef = useRef(callback);
  callbackRef.current = callback;

  const timerRef = useRef(null);

  useEffect(() => {
    return () => {
      if (timerRef.current) clearTimeout(timerRef.current);
    };
  }, []);

  return useMemo(() => {
    function debounced(...args) {
      if (timerRef.current) clearTimeout(timerRef.current);
      timerRef.current = setTimeout(() => {
        callbackRef.current(...args);
      }, delayMs);
    }
    debounced.flushNow = (...args) => {
      if (timerRef.current) clearTimeout(timerRef.current);
      callbackRef.current(...args);
    };
    return debounced;
  }, [delayMs]);
}
