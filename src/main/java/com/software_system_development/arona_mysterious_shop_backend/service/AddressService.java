package com.software_system_development.arona_mysterious_shop_backend.service;

import com.software_system_development.arona_mysterious_shop_backend.model.dto.address.AddressAddRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import com.software_system_development.arona_mysterious_shop_backend.model.dto.address.AddressUpdateRequest;
import com.software_system_development.arona_mysterious_shop_backend.model.entity.Address;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author 29967
* @description 针对表【address】的数据库操作Service
* @createDate 2024-06-05 12:28:45
*/
public interface AddressService extends IService<Address> {
    /**
     *  增加地址
     * @param addressAddRequest
     * @return
     */
    int addAddress(AddressAddRequest addressAddRequest, HttpServletRequest request);

    /**
     * 删除地址
     * @param addressId
     * @param request
     * @return
     */
    boolean deleteAddress(Integer addressId, HttpServletRequest request);

    /**
     * 根据用户ID获取地址列表
     * @return
     */
    List<Address> getAddressByUserId(HttpServletRequest request);

    /**
     * 根据地址ID获取地址信息
     */
    Address getAddressById(Integer addressId);

    /**
     * 更新地址信息
     * @param addressUpdateRequest
     * @param request
     * @return
     */
    int updateAddress(AddressUpdateRequest addressUpdateRequest, HttpServletRequest request);
}
