// GENERATED CODE - DO NOT MODIFY BY HAND
// coverage:ignore-file
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'sample_model.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

// dart format off
T _$identity<T>(T value) => value;

/// @nodoc
mixin _$SampleModel {

 int get id; String get title; String get body;
/// Create a copy of SampleModel
/// with the given fields replaced by the non-null parameter values.
@JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
$SampleModelCopyWith<SampleModel> get copyWith => _$SampleModelCopyWithImpl<SampleModel>(this as SampleModel, _$identity);

  /// Serializes this SampleModel to a JSON map.
  Map<String, dynamic> toJson();


@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is SampleModel&&(identical(other.id, id) || other.id == id)&&(identical(other.title, title) || other.title == title)&&(identical(other.body, body) || other.body == body));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,title,body);

@override
String toString() {
  return 'SampleModel(id: $id, title: $title, body: $body)';
}


}

/// @nodoc
abstract mixin class $SampleModelCopyWith<$Res>  {
  factory $SampleModelCopyWith(SampleModel value, $Res Function(SampleModel) _then) = _$SampleModelCopyWithImpl;
@useResult
$Res call({
 int id, String title, String body
});




}
/// @nodoc
class _$SampleModelCopyWithImpl<$Res>
    implements $SampleModelCopyWith<$Res> {
  _$SampleModelCopyWithImpl(this._self, this._then);

  final SampleModel _self;
  final $Res Function(SampleModel) _then;

/// Create a copy of SampleModel
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


/// Adds pattern-matching-related methods to [SampleModel].
extension SampleModelPatterns on SampleModel {
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

@optionalTypeArgs TResult maybeMap<TResult extends Object?>(TResult Function( _SampleModel value)?  $default,{required TResult orElse(),}){
final _that = this;
switch (_that) {
case _SampleModel() when $default != null:
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

@optionalTypeArgs TResult map<TResult extends Object?>(TResult Function( _SampleModel value)  $default,){
final _that = this;
switch (_that) {
case _SampleModel():
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

@optionalTypeArgs TResult? mapOrNull<TResult extends Object?>(TResult? Function( _SampleModel value)?  $default,){
final _that = this;
switch (_that) {
case _SampleModel() when $default != null:
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
case _SampleModel() when $default != null:
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
case _SampleModel():
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
case _SampleModel() when $default != null:
return $default(_that.id,_that.title,_that.body);case _:
  return null;

}
}

}

/// @nodoc
@JsonSerializable()

class _SampleModel implements SampleModel {
  const _SampleModel({required this.id, required this.title, required this.body});
  factory _SampleModel.fromJson(Map<String, dynamic> json) => _$SampleModelFromJson(json);

@override final  int id;
@override final  String title;
@override final  String body;

/// Create a copy of SampleModel
/// with the given fields replaced by the non-null parameter values.
@override @JsonKey(includeFromJson: false, includeToJson: false)
@pragma('vm:prefer-inline')
_$SampleModelCopyWith<_SampleModel> get copyWith => __$SampleModelCopyWithImpl<_SampleModel>(this, _$identity);

@override
Map<String, dynamic> toJson() {
  return _$SampleModelToJson(this, );
}

@override
bool operator ==(Object other) {
  return identical(this, other) || (other.runtimeType == runtimeType&&other is _SampleModel&&(identical(other.id, id) || other.id == id)&&(identical(other.title, title) || other.title == title)&&(identical(other.body, body) || other.body == body));
}

@JsonKey(includeFromJson: false, includeToJson: false)
@override
int get hashCode => Object.hash(runtimeType,id,title,body);

@override
String toString() {
  return 'SampleModel(id: $id, title: $title, body: $body)';
}


}

/// @nodoc
abstract mixin class _$SampleModelCopyWith<$Res> implements $SampleModelCopyWith<$Res> {
  factory _$SampleModelCopyWith(_SampleModel value, $Res Function(_SampleModel) _then) = __$SampleModelCopyWithImpl;
@override @useResult
$Res call({
 int id, String title, String body
});




}
/// @nodoc
class __$SampleModelCopyWithImpl<$Res>
    implements _$SampleModelCopyWith<$Res> {
  __$SampleModelCopyWithImpl(this._self, this._then);

  final _SampleModel _self;
  final $Res Function(_SampleModel) _then;

/// Create a copy of SampleModel
/// with the given fields replaced by the non-null parameter values.
@override @pragma('vm:prefer-inline') $Res call({Object? id = null,Object? title = null,Object? body = null,}) {
  return _then(_SampleModel(
id: null == id ? _self.id : id // ignore: cast_nullable_to_non_nullable
as int,title: null == title ? _self.title : title // ignore: cast_nullable_to_non_nullable
as String,body: null == body ? _self.body : body // ignore: cast_nullable_to_non_nullable
as String,
  ));
}


}

// dart format on
