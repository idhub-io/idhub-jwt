package io.idhub.jwt.services;

import brave.Span;
import brave.Tracer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TraceService {
    private final Tracer tracer;

    public String traceId() {
        return this.currentSpan().context().traceIdString();
    }

    protected Span currentSpan() {
        Span current = this.tracer.currentSpan();
        return current != null ? current : this.nextSpan();
    }

    protected Span nextSpan() {
        return this.start(this.tracer.nextSpan());
    }

    public Span start(Span span) {
        if (span != null) {
            span.start();
        }

        return span;
    }
}
