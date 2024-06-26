package com.software_system_development.arona_mysterious_shop_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.software_system_development.arona_mysterious_shop_backend.common.ErrorCode;
import com.software_system_development.arona_mysterious_shop_backend.exception.BusinessException;
import com.software_system_development.arona_mysterious_shop_backend.mapper.AddressMapper;
import com.software_system_development.arona_mysterious_shop_backend.model.dto.address.AddressAddRequest;
import com.software_system_development.arona_mysterious_shop_backend.model.dto.address.AddressUpdateRequest;
import com.software_system_development.arona_mysterious_shop_backend.model.entity.Address;
import com.software_system_development.arona_mysterious_shop_backend.model.enums.UserRoleEnum;
import com.software_system_development.arona_mysterious_shop_backend.model.vo.UserVO;
import com.software_system_development.arona_mysterious_shop_backend.service.AddressService;
import com.software_system_development.arona_mysterious_shop_backend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 29967
* @description 针对表【address】的数据库操作Service实现
* @createDate 2024-06-05 12:28:45
*/
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address>
    implements AddressService{

    @Resource
    UserService userService;

    @Override
    public int addAddress(AddressAddRequest addressAddRequest, HttpServletRequest request) {
        if (StringUtils.isAnyBlank(addressAddRequest.getAddressName(), addressAddRequest.getReceiver(), addressAddRequest.getUserPhone())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        Address address = new Address();
        BeanUtils.copyProperties(addressAddRequest, address);
        address.setAddressUserId(userService.getUserVO(request).getUserId());
        boolean saveResult = this.save(address);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "添加地址失败");
        }
        return address.getAddressAreaId();
    }

    @Override
    public boolean deleteAddress(Integer addressId, HttpServletRequest request) {
        // 获取当前登录用户信息
        UserVO loginUser = userService.getUserVO(request);
        // 根据地址ID查询地址信息
        Address address = this.getById(addressId);
        if (address == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "地址不存在");
        }
        // 检查有无权限删除
        if (!loginUser.getUserId().equals(address.getAddressUserId()) && !loginUser.getUserRole().equals(UserRoleEnum.ADMIN.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "无删除权限");
        }
        // 删除地址
        return this.removeById(addressId);
    }

    @Override
    public List<Address> getAddressByUserId(HttpServletRequest request) {
        // 获取当前登录用户信息
        UserVO loginUser = userService.getUserVO(request);
        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("addressUserId", loginUser.getUserId());
        List<Address> addressList = this.list(queryWrapper);
        if (addressList == null || addressList.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "未找到对应地址");
        }
        return addressList;
    }

    @Override
    public Address getAddressById(Integer addressId) {

        if (addressId == null || addressId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        Address address = this.getById(addressId);
        if (address == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "未找到对应地址");
        }
        return address;
    }


    @Override
    public int updateAddress(AddressUpdateRequest addressUpdateRequest, HttpServletRequest request) {
        if(StringUtils.isAnyBlank(addressUpdateRequest.getAddressName(), addressUpdateRequest.getReceiver(), addressUpdateRequest.getUserPhone())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        Address address = this.getById(addressUpdateRequest.getAddressId());
        if (address == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "未找到对应地址");
        }
        // 获取当前登录用户信息
        UserVO loginUser = userService.getUserVO(request);
        // 检查有无权限修改
        if (!loginUser.getUserId().equals(address.getAddressUserId()) && !loginUser.getUserRole().equals(UserRoleEnum.ADMIN.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "无修改权限");
        }
        BeanUtils.copyProperties(addressUpdateRequest, address);
        boolean updateResult = this.updateById(address);
        if (!updateResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "修改地址失败");
        }
        return address.getAddressAreaId();
    }
}




