package com.sksamuel.reactivehive

import org.apache.parquet.schema.GroupType
import org.apache.parquet.schema.MessageType
import org.apache.parquet.schema.OriginalType
import org.apache.parquet.schema.PrimitiveType

object ParquetSchemas {

  fun fromParquet(type: org.apache.parquet.schema.Type): Type {
    return when (type) {
      is PrimitiveType -> fromPrimitiveType(type)
      is MessageType -> fromGroupType(type)
      is GroupType -> fromGroupType(type)
      else -> throw UnsupportedOperationException()
    }
  }

  fun fromGroupType(messageType: GroupType): StructType {
    val fields = messageType.fields.map {
      val fieldType = fromParquet(it)
      StructField(it.name, fieldType, it.repetition == org.apache.parquet.schema.Type.Repetition.OPTIONAL)
    }
    return StructType(fields)
  }

  fun fromPrimitiveType(type: PrimitiveType): Type {
    return when (type.primitiveTypeName) {
      PrimitiveType.PrimitiveTypeName.BINARY -> {
        when (type.originalType) {
          OriginalType.UTF8 -> StringType
          // todo populate the enum values?
          OriginalType.ENUM -> EnumType(emptyList())
          else -> BinaryType
        }
      }
      PrimitiveType.PrimitiveTypeName.INT64 -> {
        when (type.originalType) {
          OriginalType.TIME_MICROS -> TimeMicrosType
          OriginalType.TIMESTAMP_MILLIS -> TimestampMillisType
          OriginalType.TIMESTAMP_MICROS -> TimestampMicrosType
          OriginalType.UINT_64 -> LongType
          OriginalType.INT_64 -> LongType
          else -> LongType
        }
      }
      PrimitiveType.PrimitiveTypeName.INT32 -> {
        when (type.originalType) {
          OriginalType.DATE -> TODO()
          OriginalType.TIME_MILLIS -> TimeMillisType
          OriginalType.UINT_8 -> ByteType
          OriginalType.UINT_16 -> ShortType
          OriginalType.UINT_32 -> IntType
          OriginalType.INT_8 -> ByteType
          OriginalType.INT_16 -> ShortType
          OriginalType.INT_32 -> IntType
          else -> IntType
        }
      }
      PrimitiveType.PrimitiveTypeName.BOOLEAN -> BooleanType
      PrimitiveType.PrimitiveTypeName.FLOAT -> FloatType
      PrimitiveType.PrimitiveTypeName.DOUBLE -> DoubleType
      PrimitiveType.PrimitiveTypeName.INT96 -> TimestampMillisType
      PrimitiveType.PrimitiveTypeName.FIXED_LEN_BYTE_ARRAY ->
        when (type.originalType) {
          OriginalType.DECIMAL -> TODO()
          else -> BinaryType
        }
    }
  }
}