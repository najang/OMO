import 'package:dio/dio.dart';

import '../models/sample_model.dart';

abstract interface class SampleRemoteDataSource {
  Future<List<SampleModel>> getSamples();
  Future<SampleModel> getSampleById(int id);
}

class SampleRemoteDataSourceImpl implements SampleRemoteDataSource {
  final Dio _dio;

  SampleRemoteDataSourceImpl(this._dio);

  @override
  Future<List<SampleModel>> getSamples() async {
    final response = await _dio.get('/samples');
    final list = response.data as List<dynamic>;
    return list.map((e) => SampleModel.fromJson(e as Map<String, dynamic>)).toList();
  }

  @override
  Future<SampleModel> getSampleById(int id) async {
    final response = await _dio.get('/samples/$id');
    return SampleModel.fromJson(response.data as Map<String, dynamic>);
  }
}
