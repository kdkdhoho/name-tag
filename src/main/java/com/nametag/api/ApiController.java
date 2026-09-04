package com.nametag.api;

import com.nametag.domain.evaluate.model.*;
import com.nametag.domain.match.model.*;
import com.nametag.domain.settle.model.*;
import java.util.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ApiController {
  private final NametagService service;

  public ApiController(NametagService s) {
    service = s;
  }

  @GetMapping("/health")
  ApiResponse<?> health() {
    return ApiResponse.ok(
        Map.of("fixture", service.isFixture(), "animals", service.animals().size()));
  }

  @GetMapping("/regions/sido")
  ApiResponse<?> sido() {
    return ApiResponse.ok(service.sido());
  }

  @GetMapping("/regions/sigungu")
  ApiResponse<?> sigungu(@RequestParam String uprCd) {
    return ApiResponse.ok(service.sigungu(uprCd));
  }

  @PostMapping("/matches")
  ApiResponse<MatchOutput> match(@RequestBody Profile p) {
    return ApiResponse.ok(service.match(p));
  }

  @PostMapping("/settle-plans")
  ApiResponse<SettlePlan> settle(@RequestBody Profile p) {
    return ApiResponse.ok(service.settle(p));
  }

  @GetMapping("/evaluations")
  ApiResponse<Evaluation> evaluate() {
    return ApiResponse.ok(service.evaluate());
  }
}
