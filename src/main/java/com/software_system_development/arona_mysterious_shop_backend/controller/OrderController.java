package com.software_system_development.arona_mysterious_shop_backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.software_system_development.arona_mysterious_shop_backend.common.BaseResponse;
import com.software_system_development.arona_mysterious_shop_backend.common.ErrorCode;
import com.software_system_development.arona_mysterious_shop_backend.common.ResultUtils;
import com.software_system_development.arona_mysterious_shop_backend.exception.BusinessException;
import com.software_system_development.arona_mysterious_shop_backend.model.dto.order.OrderAddRequest;
import com.software_system_development.arona_mysterious_shop_backend.model.dto.order.OrderUpdateRequest;
import com.software_system_development.arona_mysterious_shop_backend.model.entity.*;
import com.software_system_development.arona_mysterious_shop_backend.model.vo.OrderVO;
import com.software_system_development.arona_mysterious_shop_backend.model.vo.PageVO;
import com.software_system_development.arona_mysterious_shop_backend.model.vo.UserVO;
import com.software_system_development.arona_mysterious_shop_backend.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@Slf4j
@Tag(name = "订单接口")
public class OrderController {

    @Resource
    private OrderService orderService;
    @Resource
    private UserService userService;

    /**
     * 下订单
     * @param orderAddRequest
     * @param request
     * @return
     */
    @PostMapping("/place")
    @Operation(summary = "下订单")
    public BaseResponse<Integer> placeOrder(@RequestBody OrderAddRequest orderAddRequest, HttpServletRequest request) {
        if (orderAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = orderService.addOrder(orderAddRequest, request);
        return ResultUtils.success(result);
    }

    /**
     * 更新订单
     * @param orderUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    @Operation(summary = "更新订单")
    public BaseResponse<Integer> updateOrder(@RequestBody OrderUpdateRequest orderUpdateRequest, HttpServletRequest request) {
        if (orderUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = orderService.updateOrder(orderUpdateRequest, request);
        return ResultUtils.success(result);
    }

    /**
     * 删除订单
     * @param orderId
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @Operation(summary = "删除订单")
    public BaseResponse<Boolean> deleteOrder(@RequestParam int orderId, HttpServletRequest request) {
        if (orderId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = orderService.removeOrder(orderId, request);
        return ResultUtils.success(result);
    }

    /**
     * 获取订单详情
     * @param orderId
     * @param request
     * @return
     */
    @GetMapping("/get")
    @Operation(summary = "获取订单详情")
    public BaseResponse<OrderVO> getOrderVO(@RequestParam int orderId, HttpServletRequest request) {
        if (orderId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        OrderVO order = orderService.getOrder(orderId, request);
        return ResultUtils.success(order);
    }

    @GetMapping("/list")
    @Operation(summary = "分页获取订单列表")
    public BaseResponse<PageVO<OrderVO>> listOrderVO(@RequestParam(required = false) Integer orderId,
                                                   @RequestParam(required = false) String receiverName,
                                                   @RequestParam(required = false) String orderStatus,
                                                   @RequestParam(defaultValue = "1") long current,
                                                   @RequestParam(defaultValue = "10") long size,
                                                   HttpServletRequest request) {
        UserVO currentUser = userService.getUserVO(request);
        QueryWrapper<Order> queryWrapper = orderService.getQueryWrapper(orderId, receiverName, orderStatus)
                .eq("orderUserId", currentUser.getUserId());

        Page<Order> orderPage = orderService.page(new Page<>(current, size), queryWrapper);
        List<OrderVO> orderVOList = orderService.getOrderVO(orderPage.getRecords());
        Page<OrderVO> orderVOPage = new Page<>(current, size, orderPage.getTotal());
        orderVOPage.setRecords(orderVOList);
        PageVO<OrderVO> pageVO = new PageVO<>(orderVOPage.getRecords(), orderVOPage.getTotal());
        return ResultUtils.success(pageVO);
    }
}
