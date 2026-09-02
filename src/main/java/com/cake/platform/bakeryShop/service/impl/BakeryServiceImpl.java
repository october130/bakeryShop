package com.cake.platform.bakeryShop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cake.platform.bakeryShop.dto.bakeryShopDTO;
import com.cake.platform.bakeryShop.entity.Bakery;
import com.cake.platform.bakeryShop.entity.Category;
import com.cake.platform.bakeryShop.mapper.BakeryMapper;
import com.cake.platform.bakeryShop.mapper.CategoryMapper;
import com.cake.platform.bakeryShop.service.BakeryService;
import com.cake.platform.bakeryShop.vo.BakeryVO;
import com.cake.platform.bakeryShop.vo.CategoryVO;
import com.cake.platform.common.exception.BusinessException;
import com.cake.platform.common.result.Result;
import com.cake.platform.common.utils.UserIdUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class BakeryServiceImpl implements BakeryService {

    @Resource
    private BakeryMapper bakeryMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Result<List<CategoryVO>> listCategories() {
        // 先从 Redis 缓存查
        String cacheKey = "bakery:categories";
        List<CategoryVO> cached = (List<CategoryVO>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            log.info("分类缓存命中: {}", cacheKey);
            return Result.success(cached);
        }

        // 缓存未命中，查数据库
        List<Category> categories = categoryMapper.selectList(
                new QueryWrapper<Category>().eq("status", 1).orderByAsc("sort"));

        List<CategoryVO> voList = categories.stream()
                .map(c -> CategoryVO.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .icon(c.getIcon())
                        .build())
                .toList();

        // 写入缓存，1 小时过期
        redisTemplate.opsForValue().set(cacheKey, voList, 1, TimeUnit.HOURS);
        log.info("分类缓存写入: {}", cacheKey);

        return Result.success(voList);
    }

    @Override
    public Result<List<BakeryVO>> listBakeries(Double latitude, Double longitude, Integer categoryId) {

        // 查询所有营业中的烘焙店
        List<Bakery> bakeries = bakeryMapper.selectList(
                new QueryWrapper<Bakery>().eq("status", 1)
                        .eq(categoryId!= null, "category_id", categoryId));

        // 转换并计算距离
        List<BakeryVO> voList = bakeries.stream()
                .map(b -> {
                    BakeryVO vo = toVO(b);
                    if (latitude != null && longitude != null && b.getLatitude() != null && b.getLongitude() != null) {
                        vo.setDistance(calculateDistance(latitude, longitude, b.getLatitude(), b.getLongitude()));
                    }
                    return vo;
                })
                .sorted((a, b) -> {
                    if (a.getDistance() == null) return 1;
                    if (b.getDistance() == null) return -1;
                    return a.getDistance().compareTo(b.getDistance());
                })
                .toList();

        return Result.success(voList);
    }

    @Override
    public Result<BakeryVO> getBakeryDetail(Long bakeryId) {
        // 先从 Redis 查
        String cacheKey = "bakery:detail:" + bakeryId;
        BakeryVO cached = (BakeryVO) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            log.info("烘焙店详情缓存命中: {}", cacheKey);
            return Result.success(cached);
        }

        // 查数据库
        Bakery bakery = bakeryMapper.selectById(bakeryId);
        if (bakery == null) {
            throw new BusinessException("烘焙店不存在");
        }

        BakeryVO vo = toVO(bakery);

        // 写入缓存，30 分钟过期
        redisTemplate.opsForValue().set(cacheKey, vo, 30, TimeUnit.MINUTES);
        log.info("烘焙店详情缓存写入: {}", cacheKey);

        return Result.success(vo);
    }


    private BakeryVO toVO(Bakery bakery) {
        return BakeryVO.builder()
                .id(bakery.getId())
                .name(bakery.getName())
                .address(bakery.getAddress())
                .phone(bakery.getPhone())
                .image(bakery.getImage())
                .description(bakery.getDescription())
                .latitude(bakery.getLatitude())
                .longitude(bakery.getLongitude())
                .avgPrice(bakery.getAvgPrice())
                .status(bakery.getStatus())
                .build();
    }

    // 简化版距离计算（Haversine 公式）
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double r = 6371; // 地球半径(公里)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return r * c;
    }




//    商家端接口


    @Override
    public Result addBakery(bakeryShopDTO bakeryShopDTO) {
        if (bakeryShopDTO.getName() == null || bakeryShopDTO.getName().isBlank()) {
            return Result.error("店铺名称不能为空");
        }
        Bakery bakery = new Bakery();
        bakery.setOwnerId(UserIdUtils.getUserId());//店主为当前登录用户
        bakery.setName(bakeryShopDTO.getName());
        bakery.setAddress(bakeryShopDTO.getAddress());
        bakery.setPhone(bakeryShopDTO.getPhone());
        bakery.setImage(bakeryShopDTO.getImage());
        bakery.setDescription(bakeryShopDTO.getDescription());
        bakery.setLatitude(bakeryShopDTO.getLatitude());
        bakery.setLongitude(bakeryShopDTO.getLongitude());
        bakery.setAvgPrice(bakeryShopDTO.getAvgPrice());
        bakery.setStatus(bakeryShopDTO.getStatus() == null ? 1 : bakeryShopDTO.getStatus());//默认营业
        bakeryMapper.insert(bakery);
        log.info("开通店铺成功，店铺ID: {}", bakery.getId());
        return Result.success("开通店铺成功");
    }
    @Override
    public Result<BakeryVO> getMyBakery() {
        Bakery bakery = getMyBakeryEntity();
        log.info("获取我的店铺成功，店铺ID: {}", bakery.getId());
        return Result.success(toVO(bakery));
    }

    @Override
    public Result updateBakery(bakeryShopDTO bakeryShopDTO) {
        Bakery bakery = getMyBakeryEntity();
        if (bakeryShopDTO.getName() != null) bakery.setName(bakeryShopDTO.getName());
        if (bakeryShopDTO.getAddress() != null) bakery.setAddress(bakeryShopDTO.getAddress());
        if (bakeryShopDTO.getPhone() != null) bakery.setPhone(bakeryShopDTO.getPhone());
        if (bakeryShopDTO.getImage() != null) bakery.setImage(bakeryShopDTO.getImage());
        if (bakeryShopDTO.getDescription() != null) bakery.setDescription(bakeryShopDTO.getDescription());
        if (bakeryShopDTO.getLatitude() != null) bakery.setLatitude(bakeryShopDTO.getLatitude());
        if (bakeryShopDTO.getLongitude() != null) bakery.setLongitude(bakeryShopDTO.getLongitude());
        if (bakeryShopDTO.getAvgPrice() != null) bakery.setAvgPrice(bakeryShopDTO.getAvgPrice());
        if (bakeryShopDTO.getStatus() != null) bakery.setStatus(bakeryShopDTO.getStatus());
        bakeryMapper.updateById(bakery);
        // 清店铺详情缓存，防止读到旧数据
        redisTemplate.delete("bakery:detail:" + bakery.getId());
        log.info("编辑店铺成功，店铺ID: {}", bakery.getId());
        return Result.success("编辑店铺成功");
    }

    @Override
    public Result updateBakeryStatus(Integer status) {
        if (status == null || (status != 0 && status != 1)) {
            return Result.error("status 参数不合法(0-歇业 1-营业)");
        }
        Bakery bakery = getMyBakeryEntity();
        bakery.setStatus(status);
        bakeryMapper.updateById(bakery);
        redisTemplate.delete("bakery:detail:" + bakery.getId());
        log.info("店铺状态更新为{}，店铺ID: {}", status, bakery.getId());
        return Result.success(status == 1 ? "店铺已营业" : "店铺已歇业");
    }

    /** 查询当前登录用户的店铺 */
    private Bakery getMyBakeryEntity() {
        Bakery bakery = bakeryMapper.selectOne(
                new QueryWrapper<Bakery>().eq("owner_id", UserIdUtils.getUserId()));
        if (bakery == null) {
            throw new BusinessException("商家不存在,您还未开通商铺");
        }
        return bakery;
    }


}
