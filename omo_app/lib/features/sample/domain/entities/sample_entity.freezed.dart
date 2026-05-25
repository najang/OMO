// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'sample_entity.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;
/// @nodoc
mixin _$SampleEntity {

 int get id; String get title; String get body;
/// Create a copy of SampleEntity
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$SampleEntityCopyWith<SampleEntity> get copyWith => _$SampleEntityCopyWithImpl<SampleEntity>(this as SampleEntity, _$identity);



@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is SampleEntity&&(identical(other.id, id) || other.id == id)&&(identical(other.title, title) || other.title == title)&&(identical(other.body, body) || other.body == body));
}


@override
int get hashCode => Object.hash(runtimeType,id,title,body);

@override
String toString() {
  return 'SampleEntity(id: $id, title: $title, body: $body)';
}


}

/// @nodoc
abstract mixin class $SampleEntityCopyWith<$Res>  {
  factory $SampleEntityCopyWith(SampleEntity value, $Res Function(SampleEntity) _then) = _$SampleEntityCopyWithImpl;
@useResult
$Res call({
 int id, String title, String body
});




}
/// @nodoc
class _$SampleEntityCopyWithImpl<$Res>
    implements $SampleEntityCopyWith<$Res> {
  _$SampleEntityCopyWithImpl(this._self, this._then);

  final SampleEntity _self;
  final $Res Function(SampleEntity) _then;

/// Create a copy of SampleEntity
/// with the given fields replaced by the non-null parameter values.
@pragma('vm:prefer-inline') @override $Res call({Object? id = null,Object? title = null,Object? body = null,}) {
  return _then(_self.copyWith(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as int,title: null == title ? _self.title : title // ignore: cast_nullable_to_non_nullable
as String,body: null == body ? _self.body : body // ignore: cast_nullable_to_non_nullable
as String,
  ));
}

}


/// Adds pattern-matching-related methods to [SampleEntity].
extension SampleEntityPatterns on SampleEntity {
/// A variant of `map` that fallback to returning `orElse`.
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case final Subclass value:
///     return ...;
///   case _:
///     return orElse();
/// }
/// ```

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _SampleEntity value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _SampleEntity() when $default != null:
return $default(_that);case _:
  return orElse();

}
}
/// A `switch`-like method, using callbacks.
///
/// Callbacks receives the raw object, upcasted.
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case final Subclass value:
///     return ...;
///   case final Subclass2 value:
///     return ...;
/// }
/// ```

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _SampleEntity value)  $default,){
final _that = this;
switch (_that) {
case _SampleEntity():
return $default(_that);}
}
/// A variant of `map` that fallback to returning `null`.
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case final Subclass value:
///     return ...;
///   case _:
///     return null;
/// }
/// ```

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _SampleEntity value)?  $default,){
final _that = this;
switch (_that) {
case _SampleEntity() when $default != null:
return $default(_that);case _:
  return null;

}
}
/// A variant of `when` that fallback to an `orElse` callback.
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case Subclass(:final field):
///     return ...;
///   case _:
///     return orElse();
/// }
/// ```

@optionalTypeArgs TResult maybeWhen<TResult extends Object?>(TResult Function( int id,  String title,  String body)?  $default,{required TResult orElse(),}) {final _that = this;
switch (_that) {
case _SampleEntity() when $default != null:
return $default(_that.id,_that.title,_that.body);case _:
  return orElse();

}
}
/// A `switch`-like method, using callbacks.
///
/// As opposed to `map`, this offers destructuring.
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case Subclass(:final field):
///     return ...;
///   case Subclass2(:final field2):
///     return ...;
/// }
/// ```

@optionalTypeArgs TResult when<TResult extends Object?>(TResult Function( int id,  String title,  String body)  $default,) {final _that = this;
switch (_that) {
case _SampleEntity():
return $default(_that.id,_that.title,_that.body);}
}
/// A variant of `when` that fallback to returning `null`
///
/// It is equivalent to doing:
/// ```dart
/// switch (sealedClass) {
///   case Subclass(:final field):
///     return ...;
///   case _:
///     return null;
/// }
/// ```

@optionalTypeArgs TResult? whenOrNull<TResult extends Object?>(TResult? Function( int id,  String title,  String body)?  $default,) {final _that = this;
switch (_that) {
case _SampleEntity() when $default != null:
return $default(_that.id,_that.title,_that.body);case _:
  return null;

}
}

}

/// @nodoc


class _SampleEntity implements SampleEntity {
  const _SampleEntity({required this.id, required this.title, required this.body});
  

@override final  int id;
@override final  String title;
@override final  String body;

/// Create a copy of SampleEntity
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$SampleEntityCopyWith<_SampleEntity> get copyWith => __$SampleEntityCopyWithImpl<_SampleEntity>(this, _$identity);



@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _SampleEntity&&(identical(other.id, id) || other.id == id)&&(identical(other.title, title) || other.title == title)&&(identical(other.body, body) || other.body == body));
}


@override
int get hashCode => Object.hash(runtimeType,id,title,body);

@override
String toString() {
  return 'SampleEntity(id: $id, title: $title, body: $body)';
}


}

/// @nodoc
abstract mixin class _$SampleEntityCopyWith<$Res> implements $SampleEntityCopyWith<$Res> {
  factory _$SampleEntityCopyWith(_SampleEntity value, $Res Function(_SampleEntity) _then) = __$SampleEntityCopyWithImpl;
@override @useResult
$Res call({
 int id, String title, String body
});




}
/// @nodoc
class __$SampleEntityCopyWithImpl<$Res>
    implements _$SampleEntityCopyWith<$Res> {
  __$SampleEntityCopyWithImpl(this._self, this._then);

  final _SampleEntity _self;
  final $Res Function(_SampleEntity) _then;

/// Create a copy of SampleEntity
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? title = null,Object? body = null,}) {
  return _then(_SampleEntity(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as int,title: null == title ? _self.title : title // ignore: cast_nullable_to_non_nullable
as String,body: null == body ? _self.body : body // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
