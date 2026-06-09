import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:riverpod_annotation/riverpod_annotation.dart';

import '../../domain/entities/temp_sensitivity.dart';
import '../../domain/repositories/wardrobe_repository.dart';
import '../datasources/wardrobe_remote_datasource.dart';

part 'wardrobe_repository_impl.g.dart';

@riverpod
WardrobeRepository wardrobeRepository(Ref ref) => WardrobeRepositoryImpl(
      remoteDataSource: ref.watch(wardrobeRemoteDataSourceProvider),
    );

class WardrobeRepositoryImpl implements WardrobeRepository {
  const WardrobeRepositoryImpl({required WardrobeRemoteDataSource remoteDataSource})
      // ignore: prefer_initializing_formals
      : _remoteDataSource = remoteDataSource;

  final WardrobeRemoteDataSource _remoteDataSource;

  @override
  Future<void> setupWardrobe(List<String> itemKeys) =>
      _remoteDataSource.setupWardrobe(itemKeys);

  @override
  Future<void> initTempProfile(TempSensitivity tempSensitivity) =>
      _remoteDataSource.initTempProfile(tempSensitivity);
}
