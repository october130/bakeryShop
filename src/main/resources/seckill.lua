  -- KEYS[1] : 库存key flash:stock:{id}
  -- KEYS[2] : 已购用户set flash:bought:{id}
  -- ARGV[1] : userId
  local stock = tonumber(redis.call('get', KEYS[1]))
  if stock == nil or stock <= 0 then
      return 0--库存不存在或<=0，返回0
  end
  if (redis.call('sismember',KEYS[2],ARGV[1])==1) then
      return 2 end--判断用户是否已购，已购返回2
  redis.call('incrby',KEYS[1],-1)--库存减1,incrby key amount，redis命令
  redis.call('sadd',KEYS[2],ARGV[1])--添加已购用户 set add key value同上
  return 1--返回1
