import request from '@/utils/frontRequest'

// 获得所有场馆
export const GetAllVenue = (queryDto) => {
  return request({
    url: `/front/venues`,
    method: 'post',
    data: queryDto
  })
}

// 记录场馆访问次数
export const RecordVenueVisit = (venueId) => {
  return request({
    url: `/front/venues/visit/${venueId}`,
    method: 'post'
  })
}

// 获取当前用户收藏的场馆列表
export const GetUserCollectedVenues = () => {
  return request({
    url: `/front/venues/collect/list`,
    method: 'get'
  })
}

// 收藏场馆
export const CollectVenue = (venueId) => {
  return request({
    url: `/front/venues/collect/${venueId}`,
    method: 'post'
  })
}

// 取消收藏场馆
export const UncollectVenue = (venueId) => {
  return request({
    url: `/front/venues/unCollect/${venueId}`,
    method: 'delete'
  })
}




