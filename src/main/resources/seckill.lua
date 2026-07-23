  -- KEYS[1] : 库存key flash:stock:{id}
  -- KEYS[2] : 已购用户set flash:bought:{id}
  -- ARGV[1] : userId
  if tonumber(redis.call('get',KEYS[1]))<=0 then
      return 0--判断库存是否《=0，返回0
  end
  if (redis.call('sismember',KEYS[2],ARGV[1])==1) then
      return 2 end--判断用户是否已购，已购返回2
  redis.call('incrby',KEYS[1],-1)--库存减1,incrby key amount，redis命令
  redis.call('sadd',KEYS[2],ARGV[1])--添加已购用户 set add key value同上
  return 1--返回1
