package com.sau.gym.admin.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.mapper.RoleMapper;
import com.sau.gym.admin.mapper.RoleUserMapper;
import com.sau.gym.admin.mapper.UserMapper;
import com.sau.gym.admin.service.UserService;
import com.sau.gym.common.exception.SauException;
import com.sau.gym.model.dto.role.AssignRoleDto;
import com.sau.gym.model.dto.system.LoginDto;
import com.sau.gym.model.dto.user.UserDto;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.entity.user.User;
import com.sau.gym.model.vo.system.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/4 15:25
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleUserMapper roleUserMapper;

    @Autowired
    private RedisTemplate<String , String> redisTemplate;

    //登录接口
    @Override
    public LoginVo login(LoginDto loginDto) {
        //1.根据用户名查询用户
        User user = userMapper.selectByUserName(loginDto.getUserName());

        if (user == null){
            throw new SauException(ResultCodeEnum.LOGIN_ERROR);
        }

        //2.验证密码是否正确
        String inputPassword = loginDto.getPassword();
        String md5InputPassword = DigestUtils.md5DigestAsHex(inputPassword.getBytes());
        if(!md5InputPassword.equals(user.getPassword())) {
            throw new RuntimeException("用户名或者密码错误") ;
        }

        //3.校验验证码是否正确
        String captcha = loginDto.getCaptcha();//用户输入的验证码
        String codeKey = loginDto.getCodeKey();//redis中验证码的数据key

        //从Redis中获取验证码
        String redisCode = redisTemplate.opsForValue().get("user:login:CAPTCHA:" + codeKey);
        if(StrUtil.isEmpty(redisCode) || !StrUtil.equalsIgnoreCase(redisCode , captcha)) {
            throw new SauException(ResultCodeEnum.VALIDATECODE_ERROR);
        }

        // 验证通过删除redis中的验证码
        redisTemplate.delete("user:login:CAPTCHA:" + codeKey);

        //3.生成令牌，保存数据到Redis中
        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set("user:login:" + token , JSON.toJSONString(user) , 30 , TimeUnit.DAYS);

        //4.构建响应结果对象
        LoginVo loginVo = new LoginVo();
        loginVo.setToken(token);
        loginVo.setRefresh_token("");

        //5.返回
        return loginVo;
    }

    //获取用户信息
    @Override
    public User getUserInfo(String token) {
        String userJson = redisTemplate.opsForValue().get("user:login:" + token);
        User user = JSON.parseObject(userJson, User.class);
        return user;
    }

    //退出功能
    @Override
    public void logout(String token) {
        redisTemplate.delete("user:login:" + token);
    }

    //角色的分页查询
    @Override
    public PageInfo<User> findByPage(UserDto userDto, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<User> userList = userMapper.findByPage(userDto);
        PageInfo<User> pageInfo = new PageInfo<>(userList);
        return pageInfo;
    }

    //角色添加
    @Override
    public void saveUser(User user) {

        //1.先查数据库是否存在该用户名
        User dbUser = userMapper.selectByUserName(user.getUsername());
        if (dbUser != null){
            throw new SauException(ResultCodeEnum.USER_NAME_IS_EXISTS);
        }

        //2.存入数据库
        String password = user.getPassword();
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
        user.setPassword(md5Password);
        user.setStatus(1);

        //保存用户
        userMapper.saveUser(user);
    }

    //修改用户状态
    @Override
    public boolean updateUserStatus(Long id, String status) {
        //1.校验参数（非空/合法值）
        if (id == null || status == null) {
            return false;
        }
        //2.修改用户状态
        int row = userMapper.updateUserStatus(id,status);
        return row > 0;
    }

    //修改用户信息
    @Override
    public void updateUser(User user) {
        userMapper.updateUser(user);
    }

    //根据用户Id删除用户信息
    @Override
    public void deleteById(Long userId) {
        userMapper.deleteById(userId);
    }

    //分配角色
    @Transactional //增加事务锁机制,防止插入失败造成脏数据
    @Override
    public void doAssign(AssignRoleDto assignRoleDto) {

        //1.先删除之前的所有的用户所对应的角色数据
        roleUserMapper.deleteByUserId(assignRoleDto.getUserId());

        //2.分配新的角色数据
        List<Long> roleIdList = assignRoleDto.getRoleIdList();
        roleIdList.forEach(roleId ->{
            roleUserMapper.doAssign(assignRoleDto.getUserId(),roleId);
        });
    }

    //注册用户
    @Override
    public void register(User user) {
        //1.先查数据库是否存在该用户名
        User dbUser = userMapper.selectByUserName(user.getUsername());
        if (dbUser != null){
            throw new SauException(ResultCodeEnum.USER_NAME_IS_EXISTS);
        }

        //2.存入数据库
        String password = user.getPassword();
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
        user.setPassword(md5Password);

        //注册用户
        userMapper.register(user);
    }
}
