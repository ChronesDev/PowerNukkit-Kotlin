package cn.nukkit.event.entity

import cn.nukkit.entity.Entity

class EntityMoveByPistonEvent(entity: Entity?, pos: Vector3) : EntityMotionEvent(entity, pos), Cancellable