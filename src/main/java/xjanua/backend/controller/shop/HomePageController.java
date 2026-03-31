package xjanua.backend.controller.shop;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import xjanua.backend.dto.RestResponse;
import xjanua.backend.dto.homepage.dashboardDto;
import xjanua.backend.dto.homepage.todolistShopDto;
import xjanua.backend.service.shop.HomePageService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shop/homepage")
public class HomePageController {

    private final HomePageService homePageService;

    @GetMapping("/todo-list")
    public ResponseEntity<RestResponse<todolistShopDto>> getTodolistShop() {
        var dto = homePageService.getTodolistShop();
        return ResponseEntity.ok(RestResponse.success(dto));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<RestResponse<dashboardDto>> getDashboard(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam String period) {
        var dto = homePageService.getDashboard(startDate, endDate, period);
        return ResponseEntity.ok(RestResponse.success(dto));
    }
}