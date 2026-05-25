// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'sample_provider.dart';

// **************************************************************************
// RiverpodGenerator
// **************************************************************************

String _$getSamplesUseCaseHash() => r'58d456ff3554ddb1540091d3318f12f9469c951a';

/// See also [getSamplesUseCase].
@ProviderFor(getSamplesUseCase)
final getSamplesUseCaseProvider =
    AutoDisposeProvider<GetSamplesUseCase>.internal(
      getSamplesUseCase,
      name: r'getSamplesUseCaseProvider',
      debugGetCreateSourceHash: const bool.fromEnvironment('dart.vm.product')
          ? null
          : _$getSamplesUseCaseHash,
      dependencies: null,
      allTransitiveDependencies: null,
    );

@Deprecated('Will be removed in 3.0. Use Ref instead')
// ignore: unused_element
typedef GetSamplesUseCaseRef = AutoDisposeProviderRef<GetSamplesUseCase>;
String _$samplesHash() => r'a22ca087cb3b1039cc1f7bac855df8fa00153e3f';

/// See also [samples].
@ProviderFor(samples)
final samplesProvider = AutoDisposeFutureProvider<List<SampleEntity>>.internal(
  samples,
  name: r'samplesProvider',
  debugGetCreateSourceHash: const bool.fromEnvironment('dart.vm.product')
      ? null
      : _$samplesHash,
  dependencies: null,
  allTransitiveDependencies: null,
);

@Deprecated('Will be removed in 3.0. Use Ref instead')
// ignore: unused_element
typedef SamplesRef = AutoDisposeFutureProviderRef<List<SampleEntity>>;
// ignore_for_file: type=lint
// ignore_for_file: subtype_of_sealed_class, invalid_use_of_internal_member, invalid_use_of_visible_for_testing_member, deprecated_member_use_from_same_package
